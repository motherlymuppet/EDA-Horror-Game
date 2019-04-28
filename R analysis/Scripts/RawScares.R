scares = c(iScares, cScares)
xs = scares %>% flatten %>% map(index) %>% flatten
ys = scares %>% flatten %>% map(coredata) %>% flatten

participants = c(intervention, control)
lengths = scares %>% map(~map(.x, length) %>% reduce(sum))
ids = participants %>% map(~.x$participant$id) %>% map2(lengths, rep) %>% flatten

scareLengths = scares %>% flatten %>% map(length)
scareIds = seq(1, length(scareLengths))
groups = map2(scareIds, scareLengths, rep) %>% flatten %>% as.numeric

keep = seq(1, length(xs), 100)
xs = xs[keep] %>% as.numeric
ys = ys[keep] %>% as.numeric
ids = ids[keep] %>% as.numeric
groups = groups[keep] %>% as.numeric

chart = chartDefault +
  scale_x_continuous(name = "Time (s)", label = number_format(scale = 1e-3))+
  scale_y_continuous(name = bquote('EDA '~(10^3)), label = number_format(scale = 1e-3)) +
  scale_color_viridis_c(aes(x = xs, y = ys, color = ids)) +
  geom_path(aes(x = xs, y = ys, color = ids, group = groups)) +
  theme(legend.position = "none")

print(chart)

rm(scares, xs, ys, participants, lengths, ids, scareLengths, scareIds, groups, keep, chart)
