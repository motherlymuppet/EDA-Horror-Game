To make it work on linux:

dmesg | grep "tty"

look for ACMX

sudo ln -s /dev/ttyACM0 /dev/ttyS80
launch java application with argument "-Dgnu.ui.rxtx.SerialPorts=/dev/ttyS80"

To check:
ls -l /dev/ttyACM0 /dev/ttyS80
