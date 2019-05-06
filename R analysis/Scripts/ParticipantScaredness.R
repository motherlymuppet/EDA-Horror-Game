scaredness = intervention %>% map(~.x$participant$scaredness) %>% as.numeric
chart = chartDefault + geom_bar(aes(scaredness)) + labs(x = "Reported Fear", y = "Number of Participants")
print(chart)
rm(scaredness, chart)
