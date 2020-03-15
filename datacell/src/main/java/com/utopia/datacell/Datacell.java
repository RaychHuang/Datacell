package com.utopia.datacell;

import io.reactivex.Observable;

public interface Datacell<STATE extends State> {

  <INTENT> void sendIntent(INTENT intent);

  STATE getState();

  Observable<STATE> observeState();

  Observable<Message> observeMessage();
}
