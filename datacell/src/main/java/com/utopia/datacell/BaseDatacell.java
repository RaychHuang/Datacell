package com.utopia.datacell;

import io.reactivex.Observable;
import io.reactivex.Scheduler;
import io.reactivex.annotations.NonNull;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;

public abstract class BaseDatacell<STATE extends State> implements Datacell<STATE> {
  private final PublishSubject<Object> intentPublisher = PublishSubject.create();
  private final PublishSubject<Reducer<STATE>> reducerPublisher = PublishSubject.create();
  private final BehaviorSubject<STATE> statePublisher = BehaviorSubject.create();
  private final PublishSubject<Message> messagePublisher = PublishSubject.create();

  private final Middleware<STATE> middleware;

  public BaseDatacell(@NonNull Middleware<STATE> middleware) {
    this.middleware = middleware;
  }

  public void create() {
    Scheduler scheduler = middleware.getScheduler(this.getClass());

    DatacellShell<STATE> shell = createShell(scheduler);
    for (Seed<STATE> seed : middleware.getSeeds()) {
      seed.plant(shell);
    }

    reducerPublisher
        .observeOn(scheduler)
        .scanWith(middleware::getState, this::handleReducer)
        .subscribe(statePublisher);
  }

  public void destroy() {
    statePublisher.onComplete();
    messagePublisher.onComplete();
    intentPublisher.onComplete();
    reducerPublisher.onComplete();
  }

  @Override
  public <INTENT> void sendIntent(INTENT intent) {
    intentPublisher.onNext(intent);
  }

  @Override
  public STATE getState() {
    return statePublisher.getValue();
  }

  @Override
  public Observable<STATE> observeState() {
    return statePublisher;
  }

  @Override
  public Observable<Message> observeMessage() {
    return messagePublisher;
  }

  protected STATE handleReducer(STATE oldState, Reducer<STATE> reducer) {
    try {
      return reducer.reduce(oldState);
    } catch (Throwable e) {
      Throwable datacellException = new DatacellException(e);
      messagePublisher.onNext(() -> datacellException);
      return oldState;
    }
  }

  private DatacellShell<STATE> createShell(Scheduler scheduler) {
    return new DatacellShell<STATE>() {
      @Override
      public Observable<Object> observeIntent() {
        return intentPublisher.observeOn(scheduler);
      }

      @Override
      public void postReducer(Reducer<STATE> reducer) {
        reducerPublisher.onNext(reducer);
      }

      @Override
      public void postMessage(Message message) {
        scheduler.scheduleDirect(() -> messagePublisher.onNext(message));
      }

      @Override
      public <INTENT> void sendIntent(INTENT intent) {
        intentPublisher.onNext(intent);
      }

      @Override
      public STATE getState() {
        return statePublisher.getValue();
      }

      @Override
      public Observable<STATE> observeState() {
        return statePublisher;
      }

      @Override
      public Observable<Message> observeMessage() {
        return messagePublisher;
      }
    };
  }
}
