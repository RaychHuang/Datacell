package com.utopia.datacell;

public interface Seed<STATE extends State> {

  void plant(DatacellShell<STATE> shell);
}
