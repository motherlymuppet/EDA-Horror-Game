scaredness = intervention %>% map(~.x$participant$scaredness) %>% as.numeric
chart = chartDefault + geom_bar(aes(scaredness)) + labs(x = "On a scale of 1-5, how scared are you of horror games / films in general?", y = "Frequency")
print(chart)
rm(scaredness, chart)
