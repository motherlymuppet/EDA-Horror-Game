i = iZoos %>% map(~getZooValue(.x, 0)) %>% unlist
c = cZoos %>% map(~getZooValue(.x, 0)) %>% unlist

y = c(i,c)

group = c(
  rep("Intervention", length(i)),
  rep("Control", length(c))
)
  
chart = chartDefault + 
  scale_y_continuous(name = bquote('Starting EDA '~(10^3)), label = number_format(scale = 1e-3), limits = c(0, 225e3)) +
  stat_boxplot(aes(x = group, y = y), geom ='errorbar', width = 0.3) +
  geom_boxplot(aes(x = group, y = y, fill = group)) +
  labs(x = "Group") +
  theme(legend.position = "none")

print(chart)

rm(i, c, y, group, chart)