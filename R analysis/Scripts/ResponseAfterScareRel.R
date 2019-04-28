getSeries = function(scares){
  scares = tail(scares, -3)
  scares = flatten(scares)
  scares = map(scares, normaliseX)
  scares = map(scares, normaliseYRel)
  
  allX = getAllX(scares)
  scares = interpolate(scares, allX)
  series = aggregateZoos(scares, meanAndStdErr)
  return(series)
}

iSeries = getSeries(iScares)
cSeries = getSeries(cScares)

allAes = aes(x = iSeries$mean %>% index)

chart = chartDefault + allAes +
  scale_x_continuous(name = "Time after Scare (s)", label = number_format(scale = 1e-3), breaks = seq(0,1e4,len=11)) +
  scale_y_continuous(name = bquote("EDA Change (%)"), label = number_format(scale = 100)) +
  
  geom_line(aes(y = iSeries$mean %>% coredata, colour="Intervention")) +
  geom_ribbon(aes(ymin = iSeries$lowError, ymax = iSeries$highError, fill="Intervention"), alpha=2/10) +
  
  geom_line(aes(y = cSeries$mean %>% coredata, colour="Control")) +
  geom_ribbon(aes(ymin = cSeries$lowError, ymax = cSeries$highError, fill="Control"), alpha=2/10)

print(chart)

rm(iSeries, cSeries, allAes, getSeries, chart)