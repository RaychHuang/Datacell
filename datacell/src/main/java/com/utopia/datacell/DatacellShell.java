package com.utopia.datacell;

import io.reactivex.Observable;

public interface DatacellShell<STATE extends State> extends Datacell<STATE> {

  Observable<Object> observeIntent();

  void postReducer(Reducer<STATE> reducer);

  void postMessage(Message message);
}
