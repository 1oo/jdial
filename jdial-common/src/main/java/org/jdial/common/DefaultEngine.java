package org.jdial.common;

import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import org.joda.time.Period;
import org.slf4j.Logger;

class DefaultEngine extends TimerTask implements Engine {

  static class DefaultEngineFactory implements Engine.Factory {
    @Inject
    private Logger logger;

    @Override
    public Engine create(Runnable owner, Period period, boolean isDaemon) {
      return new DefaultEngine(logger, owner, period, isDaemon);
    }
  }

  private final Logger logger;
  private final Runnable owner;
  private final Timer timer;
  private final Period period;

  DefaultEngine(Logger logger, Runnable owner, Period period, boolean isDaemon) {
    this(logger, new Timer("Engine-" + owner.getClass().getSimpleName(), isDaemon), owner, period);
  }

  DefaultEngine(Logger logger, Timer timer, Runnable owner, Period period) {
    this.logger = logger;
    this.owner = owner;
    this.timer = timer;
    this.period = period;
  }

  @Override
  public void start() {
    timer.schedule(this, 0, period.toStandardSeconds().getSeconds() * 1000);
    logger.debug("started engine to {}", owner.getClass().getSimpleName());
  }

  @Override
  public void stop() {
    timer.cancel();
    logger.debug("stop engine to {}", owner);
  }

  @Override
  public void run() {
    try {
      owner.run();
    } catch (Exception e) {
      logger.error(e.getMessage(), e);
    }
  }

}