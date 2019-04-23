zooRange = function(zoo){
  minX = start(zoo)
  
  minY = zoo %>% coredata %>% reduce(min)
  xOfMinY = xOfMinY(zoo)
  
  range = list(minX, xOfMinY)
  zooBeforeMin = zooWindow(zoo, range)
  maxY = zooBeforeMin %>% reduce(max)
  drop = maxY - minY
  
  list(scare = minX, drop = drop) %>% return
}

getSeries = function(scares){
  scares = scares %>% map(~.x[1:3])
  scares = flatten(scares)
  ranges = scares %>% map(zooRange) %>% map(~list(x = .x$scare, y = .x$drop))
  ranges %>% transpose %>% map(as.numeric) %>% return
}

iSeries = getSeries(iScares)
cSeries = getSeries(cScares)

model = function(series){
  mod <- lm(y ~ x, data = series)
  
  newX <- seq(min(series$x), max(series$x), length.out=100)
  preds <- predict(mod, newdata = data.frame(x=newX), interval = 'confidence')
  
  list(model = mod, x = newX, line = preds[,1], lower = preds[,3], higher = preds[,2]) %>% return
}

ilm = model(iSeries)
clm = model(cSeries)

chart = chartDefault + #allAes +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3)) +
  scale_y_continuous(name = "EDA Drop (thousand)", label = number_format(scale = 1e-3)) +
  
  geom_point(aes(x = iSeries$x, y = iSeries$y, colour = "Intervention"), shape = 4)+
  geom_point(aes(x = cSeries$x, y = cSeries$y, colour = "Control"), shape = 4)+
  
  geom_line(aes(x = ilm$x, y = ilm$line, color = "Intervention")) +
  geom_ribbon(aes(ilm$x, ymin = ilm$lower, ymax = ilm$higher, fill="Intervention"), alpha=2/10) +
  
  geom_line(aes(x = clm$x, y = clm$line, color = "Control")) +
  geom_ribbon(aes(x = clm$x, ymin = clm$lower, ymax = clm$higher, fill="Control"), alpha=2/10) +

  labs(title = "Difference between groups", y = "Difference in EDA between groups")

print(chart)

rm(ilm, clm, model, iSeries, cSeries, getSeries, zooRange, chart)