#!/usr/bin/python2

import sys
import csv

file_path_name = sys.argv[1]+'/transaction_results.csv'
print('Processing '+file_path_name)

# initializing the titles and rows list 
fields = [] 

min_begin_time = 0
max_end_time = 0
num_tx = 0

num_query=0
total_query_response_time=0

num_workload=0
total_workload_response_time = 0
  
# reading csv file 
with open(file_path_name, 'r') as csvfile: 
    # creating a csv reader object 
    csvreader = csv.reader(csvfile) 
      
    # extracting field names through first row 
    fields = csvreader.next() 

    first_line = csvreader.next() 

    min_begin_time = long(first_line[4])
    max_end_time = long(first_line[5])
    num_tx = num_tx + 1

    if first_line[1] == "SELECT":
        num_query = num_query + 1
        total_query_response_time = total_query_response_time + (long(first_line[5]) - long(first_line[4]))

    num_workload = num_workload + long(first_line[2])
    total_workload_response_time = total_workload_response_time + (long(first_line[5]) - long(first_line[4]))



  
    # extracting each data row one by one 
    for row in csvreader: 
        if len(row) < 7:
            break

        if long(row[4]) < min_begin_time:
            min_begin_time = long(row[4])
        
        if long(row[5])>max_end_time:
            max_end_time = long(row[5])

        num_tx = num_tx + 1

        if row[1] == "QUERY":
            num_query = num_query + 1
            total_query_response_time = total_query_response_time + (long(row[5]) - long(row[4]))

        num_workload = num_workload + long(row[2])
        total_workload_response_time = total_workload_response_time + (long(row[5]) - long(row[4]))



throughput = float(num_tx) / (max_end_time-min_begin_time)
avg_response_time_workload = float(total_workload_response_time) / num_workload
avg_response_time_query = float(total_query_response_time) / num_query

print("throughput: " + str(throughput) + " txs per ms" + " | " + "avg_response_time_query: " + str(avg_response_time_query) + " ms" + " | " + "avg_response_time_workload: " + str(avg_response_time_workload) + " ms")
