#  TODO histogram of intervention and control EDA values at 0 - 2 graphs. Compare values. C has more low values, could partly explain the bigger drops in intervention

i = iZoos %>% map(~getZooValue(.x, 0)) %>% unlist
c = cZoos %>% map(~getZooValue(.x, 0)) %>% unlist

y = c(i,c)

group = c(
  rep("Intervention", length(i)),
  rep("Control", length(c))
)
  
chart = chartDefault + 
  scale_y_continuous(name = "Starting EDA (thousand)", label = number_format(scale = 1e-3), limits = c(0, 225e3)) +
  stat_boxplot(aes(x = group, y = y), geom ='errorbar', width = 0.3) +
  geom_boxplot(aes(x = group, y = y, fill = group)) +
  labs(title = "Comparing Starting EDA between groups", x = "Group") +
  theme(legend.position = "none")

print(chart)