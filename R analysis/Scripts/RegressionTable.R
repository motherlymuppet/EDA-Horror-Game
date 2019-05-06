i = interventionRegression
c = controlRegression

m = c("Intervention", "Control")
p = c(pValue(i), pValue(c))
r = c(rSq(i), rSq(c))
g = c(gradient(i), gradient(c)) * 1000

df = data.frame(m, g, r, p)
names(df) = c("Model", "Gradient", "R-Squared", "P-Value")

regressionTable = kable(df, "latex", booktabs = T, align = c("c")) %>% 
  row_spec(row = 0, bold = T) %>%

print(regressionTable)

rm(i, c, m, p, r, g, regressionTable, df)