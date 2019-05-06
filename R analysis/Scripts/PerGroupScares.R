scares = c(iScares, cScares)
xs = scares %>% flatten %>% map(index) %>% flatten
ys = scares %>% flatten %>% map(coredata) %>% flatten

scareLengths = scares %>% flatten %>% map(length)
scareIds = seq(1, length(scareLengths))
groups = map2(scareIds, scareLengths, rep) %>% flatten %>% as.numeric

iLength = iScares %>% flatten %>% map(length) %>% reduce(sum)
cLength = cScares %>% flatten %>% map(length) %>% reduce(sum)

color = c(
  rep("Intervention", iLength),
  rep("Control", cLength)
)

keep = seq(1, length(xs), 100)
xs = xs[keep] %>% as.numeric
ys = ys[keep] %>% as.numeric
groups = groups[keep] %>% as.numeric
color = color[keep]

chart = chartDefault +
  scale_x_continuous(name = "Time After Playtest Start (s)", label = number_format(scale = 1e-3))+#, breaks = seq(start,end,len=11)) +
  scale_y_continuous(name = bquote('EDA '~(10^3)), label = number_format(scale = 1e-3)) +
  geom_path(aes(x = xs, y = ys, color = color, group = groups))

print(chart)

rm(scares, xs, ys, scareLengths, scareIds, groups, iLength, cLength, color, keep, chart)
