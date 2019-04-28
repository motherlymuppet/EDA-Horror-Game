zoos = c(iZoos, cZoos)
xs = zoos %>% map(index) %>% flatten
ys = zoos %>% map(coredata) %>% flatten

participants = c(intervention, control)
lengths = zoos %>% map(length)
ids = participants %>% map(~.x$participant$id) %>% map2(lengths, rep) %>% flatten

keep = seq(1, length(xs), 100)
xs = xs[keep] %>% as.numeric
ys = ys[keep] %>% as.numeric
ids = ids[keep] %>% as.numeric

scares = c(iScares, cScares)
scareTimes = scares %>% flatten %>% map(~start(.x)) %>% as.numeric
scareValues = scares %>% flatten %>% map(~.x[[1]]) %>% as.numeric

chart = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3))+#, breaks = seq(start,end,len=11)) +
  scale_y_continuous(name = bquote('EDA '~(10^3)), label = number_format(scale = 1e-3)) +
  scale_color_viridis_c(aes(x = xs, y = ys, color = ids)) +
  geom_path(aes(x = xs, y = ys, color = ids, group = ids)) +
  theme(legend.position = "none")

print(chart)

rm(zoos, xs, ys, participants, lengths, ids, keep, scares, scareTimes, scareValues, chart)
