import requests
from requests.auth import HTTPBasicAuth
import json
import config

url = ""

auth = HTTPBasicAuth(str(config.username), API_Token)

headers = {
   "Accept": "application/json",
   "Content-Type": "application/json"
}

payload = json.dumps({
    "notificationScheme": ,
    "description": "",
    "leadAccountId": "",
    "url": "",
    "projectTemplateKey": "com.pyxis.greenhopper.jira:gh-simplified-basic",
    "avatarId": None,
    "issueSecurityScheme": None,
    "name": "Test Project 2",
    "permissionScheme": 10000,
    "assigneeType": "UNASSIGNED",
    "projectTypeKey": "software",
    "key": "TEST1",
    "categoryId": None
})

response = requests.request(
   "POST",
   url,
   data= payload,
   headers= headers,
   auth= auth
)

print(response)
# print(response.text)

print(json.dumps(json.loads(response.text), sort_keys=True, indent=4, separators=(",", ": "))) #Formatting json data