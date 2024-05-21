import http.client
import http.cookiejar
import json
import ssl
import time
import urllib.request

import requests

def call_for_pid():
    url = "https://pass.rzd.ru/timetable/public/?layer_id=5827&dir=0&tfl=3&checkSeats=1&code0=2004000&code1=2000000&dt0=01.06.2024&md=0"
    s = requests.Session()
    headers = {
        'User-Agent': "PostmanRuntime/7.28.4",
    }
    response = s.request("GET", url, headers=headers).json()
    print(response["RID"])

    url = "https://pass.rzd.ru/timetable/public/ru?layer_id=5827"
    payload = 'rid={}'.format(response["RID"])
    headers = {
        'User-Agent': "PostmanRuntime/7.28.4",
        'Content-Type': 'application/x-www-form-urlencoded'
    }

    for i in range(5):
        time.sleep(0.5)
        response = s.request("POST", url, headers=headers, data=payload, cookies=s.cookies)
        if response.status_code != 200:
            break
        response_json = response.json()
        if response_json['result'] == 'OK':
            break

    response_json = response.json()
    if response_json['result'] != 'OK':
        return -1
    response

    cases = response.json()['tp'][0]['list']

    print(response.json())

if __name__ == '__main__':
    call_for_pid()

