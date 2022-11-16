package at.clanattack.central.top.start

import at.clanattack.central.start.IStartServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider

val projectStarted = getServiceProvider<IStartServiceProvider>().started

val projectStartTime = getServiceProvider<IStartServiceProvider>().startTime

val timeTillProjectStart = projectStartTime - System.currentTimeMillis()

fun setProjectStartTime(startTime: Long) = getServiceProvider<IStartServiceProvider>().setStartTime(startTime)
