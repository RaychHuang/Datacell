package com.utopia.datacell;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public abstract class Middleware<STATE extends State> {
  private final List<Seed<STATE>> seeds = new ArrayList<>();

  abstract STATE getState();

  List<Seed<STATE>> getSeeds() {
    return seeds;
  }

  protected Scheduler getScheduler(Class clazz) {
    String name = "DatacellThread - " + clazz.getSimpleName();
    return Schedulers.from(Executors.newSingleThreadExecutor(r -> new Thread(r, name)));
  }
}
