#!/usr/bin/python2

import sys
import csv
import fnmatch
import os 

result_dir_path_base = "/home/guoxi/Workspace/cs223/results"

def get_path_for_exp(db,workload,policy,mpl,isolation):
    exp_prefix = "d_"+db+"|"+"w_"+workload+"|"+"p_"+policy+"|"+"m_"+mpl+"|"+"i_"+isolation+"|"
    exp_path_regx = exp_prefix + "*"
    exp_dir_list = []
    for file in os.listdir(result_dir_path_base):
        if fnmatch.fnmatch(file, exp_path_regx):
            #print file
            exp_dir_list.append(file)

    if len(exp_dir_list) == 0:
        print("Experiment with prefix: " + exp_prefix + " Not Found") 
    else:
        return result_dir_path_base + "/" + exp_dir_list[0]

def analyze_exp(exp_dir_path):
    file_path_name = exp_dir_path+'/transaction_results.csv'
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
    return [throughput,avg_response_time_query,avg_response_time_workload]

def gen_exp_1_response_type_wokload(t,workload):
    if t == "query":
        csv_file_name = "exp_1_response_query_w"+workload+".csv"
    elif t == "workload":
        csv_file_name = "exp_1_response_workload_w"+workload+".csv"
    else:
        print("TYPE " + t + " NOT SUPPORTED")

    fields = ["mpl","postgres-single", "postgres-batch", "mysql-single", "mysql-batch"]
    isolation = "2"
    with open(csv_file_name, 'w') as csvfile: 
        csvwriter = csv.writer(csvfile) 
        csvwriter.writerow(fields) 
        for mpl in [2,4,8,16,32,64,128]:
            #each row
            row = [0,0,0,0,0]
            col_index = 0

            row[col_index] = str(mpl)
            col_index = col_index + 1

            for db in ["postgres","mysql"]:
                for policy in ["single","batch"]:
                    exp_path = get_path_for_exp(db,workload,policy,str(mpl),"2")
                    res = analyze_exp(exp_path)
                    if t == "query":
                        data = str(res[1])
                    elif t == "workload":
                        data = str(res[2])
                    row[col_index] = data # change for different result
                    col_index = col_index + 1
            
            csvwriter.writerow(row) 
            
# gen_exp_1_response_type_wokload("query","low")
# gen_exp_1_response_type_wokload("query","high")
# gen_exp_1_response_type_wokload("workload","low")
# gen_exp_1_response_type_wokload("workload","high")


def gen_exp_2_throughput_wokload(workload):
    csv_file_name = "exp_2_throughput_w"+workload+".csv"
    fields = ["mpl","postgres-RU", "postgres-RC", "postgres-RR", "postgres-S","mysql-RU", "mysql-RC", "mysql-RR", "mysql-S"]
    policy = "batch"
    with open(csv_file_name, 'w') as csvfile: 
        csvwriter = csv.writer(csvfile) 
        csvwriter.writerow(fields) 
        for mpl in [4,8,16,32,64,128]:
            #each row
            row = [0,0,0,0,0,0,0,0,0]
            col_index = 0

            row[col_index] = str(mpl)
            col_index = col_index + 1

            for db in ["postgres","mysql"]:
                for isolation in ["1","2", "4", "8"]:
                    exp_path = get_path_for_exp(db,workload,policy,str(mpl),isolation)
                    res = analyze_exp(exp_path)
                    data = str(res[0]*1000)
                    # if t == "query":
                    #     data = str(res[1])
                    # elif t == "workload":
                    #     data = str(res[2])
                    row[col_index] = data # change for different result
                    col_index = col_index + 1
            
            csvwriter.writerow(row) 

gen_exp_2_throughput_wokload("low")
gen_exp_2_throughput_wokload("high")

def gen_exp_2_response_type_wokload(t,workload):
    if t == "query":
        csv_file_name = "exp_2_response_query_w"+workload+".csv"
    elif t == "workload":
        csv_file_name = "exp_2_response_workload_w"+workload+".csv"
    else:
        print("TYPE " + t + " NOT SUPPORTED")

    fields = ["mpl","postgres-RU", "postgres-RC", "postgres-RR", "postgres-S","mysql-RU", "mysql-RC", "mysql-RR", "mysql-S"]
    policy = "batch"
    with open(csv_file_name, 'w') as csvfile: 
        csvwriter = csv.writer(csvfile) 
        csvwriter.writerow(fields) 
        for mpl in [4,8,16,32,64,128]:
            #each row
            row = [0,0,0,0,0,0,0,0,0]
            col_index = 0

            row[col_index] = str(mpl)
            col_index = col_index + 1

            for db in ["postgres","mysql"]:
                for isolation in ["1","2", "4", "8"]:
                    exp_path = get_path_for_exp(db,workload,policy,str(mpl),isolation)
                    res = analyze_exp(exp_path)
                    if t == "query":
                        data = str(res[1])
                    elif t == "workload":
                        data = str(res[2])
                    row[col_index] = data # change for different result
                    col_index = col_index + 1
            
            csvwriter.writerow(row) 

# gen_exp_2_response_type_wokload("query","low")
# gen_exp_2_response_type_wokload("query","high")
# gen_exp_2_response_type_wokload("workload","low")
# gen_exp_2_response_type_wokload("workload","high")