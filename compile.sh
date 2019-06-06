mvn compile

TIME=3

# Maquinas do modelo
xterm -hold -e mvn exec:java@Model 2>/dev/null  &
sleep $TIME

# Maquinas do controle
xterm -hold -e mvn exec:java@Control 2>/dev/null  &
sleep $TIME

# Maquinas da visão
xterm -hold -e mvn exec:java@View 2>/dev/null  &
sleep $TIME