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

chart = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3), breaks = seq(start,end,len=11)) +
  scale_y_continuous(name = bquote('EDA Drop '~(10^3)), label = number_format(scale = 1e-3), limits = c(0, 5e4)) +
  
  geom_point(aes(x = iSeries$x, y = iSeries$y, colour = "Intervention"), shape = 4)+
  geom_point(aes(x = cSeries$x, y = cSeries$y, colour = "Control"), shape = 4)+
  
  stat_smooth(aes(x=iSeries$x, y=iSeries$y), method="lm", col=interventionColor, fill=interventionColor, n=1e3) +
  stat_smooth(aes(x=cSeries$x, y=cSeries$y), method="lm", col=controlColor, fill=controlColor, n=1e3)
  
print(chart)

interventionRegression = ilm$model
controlRegression = clm$model
rm(ilm, clm, model, iSeries, cSeries, getSeries, zooRange, chart)