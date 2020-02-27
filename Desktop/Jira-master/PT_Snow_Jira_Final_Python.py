import requests
import logging
import os.path
import xml.etree.ElementTree as ET
import csv
import pandas as pd
import re
import config
import paramiko

from jira import JIRA
from jira import JIRAError

def start_conn():
    u_name = config.username
    pswd = config.password
    port = 22
    host_name = ''
    
    my_conn = paramiko.SSHClient()
    my_conn.set_missing_host_key_policy(paramiko.AutoAddPolicy)

    session = my_conn.connect(hostname= host_name, port= port, username= u_name, password= pswd)

    remote_cmd = 'ifconfig'
    (stdin, stdout, stderr) = my_conn.exec_command(remote_cmd)
    print("{}".format(stdout.read()))
    print("{}".format(type(my_conn)))
    print("Options available \n{}".format(dir(my_conn)))

    ftp_client = my_conn.open_sftp()
    ftp_client.put('C:/Users/akagarw/Desktop/Work/Scripts/Product Line/Final/PT_Names_Final.csv', '/var/jira/inactive-list/PT_Names.csv')
    # Permission Denied
    # ftp_client.get('/home/users/akagarw/PT_Names_GroupID.csv', 'C:/Users/akagarw/Desktop/Work/Scripts/Product Line/SNOW API/')
    ftp_client.close()

    my_conn.close()

def fetch_data():
    #Directory
    save_path = "C:\\Users\\akagarw\\Desktop\\Work\\Scripts\\Product Line\\Final"

    # Set the request parameters
    url = ''

    # Eg. User name="username", Password="password" for this code sample.
    user = ''
    pwd = ''

    # Set proper headers
    headers = {"Accept" : "application/xml"}

    # Do the HTTP request
    response = requests.get(url, auth=(user, pwd), headers=headers)

    # Check for HTTP codes other than 200
    if response.status_code != 200:
        print('Status:', response.status_code, 'Headers:', response.headers, 'Error Response:', response.content)
        print("\n Failed... \n")
        exit()

    # Decode the XML response into a dictionary and use the data
    # print(response.content.title("u_name"))
    """ test = response.content """

    #Create Files
    # filePath = os.path.join(save_path, "PT_Names.txt")
    filePath = os.path.join(save_path, "PT_Names_New.csv")

    #Formatting to build XML
    xml = str(response.content)
    xml = xml.replace("'","b")
    xml = xml.strip('b')

    #Parsing XML
    root = ET.fromstring(xml)

    names_PT = ["PT Name"]
    snow_id = ["SNOW ID"]
    jira_id = ["JIRA ID"]

    #Finding all u_name and fetching PT names
    for result in root.findall('result'):
        if(result.find('u_active').text == 'true'):
            name = result.find('u_name').text
            names_PT.append(name)
            sys_id = result.find('sys_id').text
            snow_id.append(sys_id)
            jira_id.append("")

    #Creating a text file with PT names
    with open(filePath, "w", newline= '') as f:
        writer = csv.writer(f, delimiter=',')
        data = list(zip(names_PT, snow_id, jira_id)) #Zip group_id if reqd
        for row in data:
            row = list(row)
            writer.writerow(row)

    infile = "C:/Users/akagarw/Desktop/Work/Scripts/Product Line/Final/PT_Names_Current.csv"
    newfile = "C:/Users/akagarw/Desktop/Work/Scripts/Product Line/Final/PT_Names_New.csv"

    outfile = "C:/Users/akagarw/Desktop/Work/Scripts/Product Line/Final/PT_Names_Final.csv"

    f1 = pd.read_csv(infile)
    f2 = pd.read_csv(newfile)

    list_name = []
    list_id = []
    list_jira = []

    new_id = []
    new_name = []
    new_jira = []

    for id1, name1 in f2.iterrows():
        # print(str(name['PT Name']))
        s_id1 = name1['SNOW ID']
        newfile_name = name1['PT Name']
        flag = 0

        for id2, name2 in f1.iterrows():
            s_id2 = name2['SNOW ID']
            if s_id1 == s_id2:
                flag = 1
                list_name.append(name1['PT Name'])
                list_id.append(name1['SNOW ID'])
                list_jira.append(name2['JIRA ID'])
                break
            else:
                flag = 0
        if flag == 0:
            new_id.append(s_id1)
            new_name.append(newfile_name)
            new_jira.append("")
            break
    
    list_name = list_name + new_name
    list_id = list_id + new_id
    list_jira = list_jira + new_jira


    dict = {'PT Name': list_name, 'SNOW ID': list_id, 'JIRA ID': list_jira}

    df = pd.DataFrame(dict)

    df.to_csv(outfile)

if __name__ == "__main__":
    fetch_data()
    start_conn()