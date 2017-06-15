package com.twitter.finagle.stats

/**
 * A blacklisting [[StatsReceiver]].  If the name for a metric is found to be
 * blacklisted, nothing is recorded.
 *
 * @param self a base [[StatsReceiver]], used for metrics that aren't
 *        blacklisted
 * @param blacklisted a predicate that reads a name and returns true to
 *        blacklist, and false to let it pass through
 */
class BlacklistStatsReceiver(
    protected val self: StatsReceiver,
    blacklisted: Seq[String] => Boolean)
  extends StatsReceiverProxy {

  override def counter(name: String*): Counter =
    getStatsReceiver(name).counter(name: _*)

  override def stat(name: String*): Stat =
    getStatsReceiver(name).stat(name: _*)

  override def addGauge(name: String*)(f: => Float): Gauge =
    getStatsReceiver(name).addGauge(name: _*)(f)

  private[this] def getStatsReceiver(name: Seq[String]): StatsReceiver =
    if (blacklisted(name)) NullStatsReceiver else self

  override def toString: String = s"BlacklistStatsReceiver($self)"
}
