# load-generator

A Python script to preload the state databases with documents to test performance and reporting requirements.

### **Running load generator locally**
Go to directory load-generator and run:
```
pipenv install
```
A virtual environment is created. In IntelliJ go to File -> ProjectStructure and add the virtual environment 
as new python SDK. 

Create configuration to run the main.py, using the virtual environment, with parameters (example with values):
```
--sis_init 3 
--sis_count 5
--sms_init 4 
--sms_count 5 
--tn_init 6 
--tn_count 5 
--sender TES5 
--recipient XX11 
--timestamp "2020-12-11 10:34:12" 
--state_type outbound 
--batch_size 100
```
Timestamp works with " ", while for ' ' or without quotes it cannot be parsed. 

Default mongodb location is localhost:27017. If you use another, add host and port to parameters:
```
--host name_of_host
--post nr_of_port
```
