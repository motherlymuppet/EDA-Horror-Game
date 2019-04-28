i = intervention %>% map(~.x$participant$enjoyment) %>% as.numeric
c = control %>% map(~.x$participant$enjoyment) %>% as.numeric

y = c(i,c)

group = c(
  rep("Intervention", length(i)),
  rep("Control", length(c))
)

chart = chartDefault + 
  stat_boxplot(aes(x = group, y = y), geom ='errorbar', width = 0.3) +
  geom_boxplot(aes(x = group, y = y, fill = group)) +
  theme(legend.position = "none") +
  labs(x = "Group", y = "Response")

print(chart)

rm(i, c, y, group, chart)