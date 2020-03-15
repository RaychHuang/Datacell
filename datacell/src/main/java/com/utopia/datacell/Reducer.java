package com.utopia.datacell;

public interface Reducer<STATE extends State> {

  STATE reduce(STATE state);
}
