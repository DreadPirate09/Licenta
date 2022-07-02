from flask import Flask, jsonify, request, render_template
import requests
from PIL import Image
import json
import base64
from io import BytesIO
import numpy as np
import os
import cv2
import sys
sys.path.append('C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src')
import faces
import facestrain

app = Flask(__name__)
ALLOW = 0
PERSON = "unknown"
count = 0
newAddedImages = 0
LOCKED_FLAG = 0
IP_ADDRESS = "http://192.168.100.53:4040"
just_added = 0

person_allowed = ['georgian','Ben_Afflek','georgi']

@app.route("/faceRecognition", methods=["GET", "POST"])
def faceRecognition():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    img = dict_data["img"] 

    img = base64.b64decode(img)
    data_np = np.fromstring(img, dtype='uint8')
    decimg = cv2.imdecode(data_np, 1)
    cv2.imwrite("recived.png", decimg)
    img = cv2.imread("recived.png")
    name = faces.execute(img)
    print(name)
    print("persons allowed " ,person_allowed)

    if name in person_allowed :
        response = {
            "result":"allowed unlocking",
            "name":name
            }

        LOCKED_FLAG = 0
        r = requests.post(IP_ADDRESS+"/unlockPi", json=json.dumps(response))
    else :

        if name == "no face detected":
            response = {
                "result":"",
                "name":"no face detected"
                }
        else:
            response = {
                "result":"not allowed",
                "name":"unkown"
                }


    return jsonify(response)


@app.route("/addImages", methods=["GET", "POST"])
def addImages():
    json_data = request.get_json()
    dict_data = json.loads(json.dumps(json_data))


    img = dict_data["img"] 
    directoryReq = dict_data["directory"]
    print(directoryReq)

    img = base64.b64decode(img)
    data_np = np.fromstring(img, dtype='uint8')
    decimg = cv2.imdecode(data_np, 1)
    print(dict_data["nr"])
    directory = "C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\images\\"+str(directoryReq)
    nameImg ="\\new" + str(dict_data["nr"]) + ".jpg"
    cv2.imwrite(directory+nameImg, decimg)

    response = {
        "img":"added",
        "number img ":dict_data["nr"]    
        }

    return jsonify(response)

@app.route("/createFolderName", methods=["GET", "POST"])
def createFolderName():
    global person_allowed
    just_added = 1
    json_data = request.get_json()
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    directory = dict_data["name"]
    person_allowed.append(directory)
    parent_dir = "C:\\GitRepo1\\Licenta\\Licenta\\Server\\Recognizer\\src\\images"
    path = os.path.join(parent_dir, directory)
    os.mkdir(path)

    response = {
        "create":"success"    
        }

    return jsonify(response)

@app.route("/trainModel", methods=["GET", "POST"])
def trainModel():
    json_data = request.get_json()
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    facestrain.execute()

    response = {
        "trained":"success"    
        }

    return jsonify(response)

@app.route("/unlock", methods=["GET", "POST"])
def unlock():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    jsonVal = {
    "action":"unlock"
    }

    LOCKED_FLAG = 0

    r = requests.post(IP_ADDRESS+"/unlockPi", json=json.dumps(jsonVal))

    response = {
        "action":"unlock"
        }

    return jsonify(response)

@app.route("/lock", methods=["GET", "POST"])
def lock():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    jsonVal = {
    "action":"lock"
    }

    LOCKED_FLAG = 1

    r = requests.post(IP_ADDRESS+"/lockPi", json=json.dumps(jsonVal))

    response = {
    "action":"lock"
    }

    return jsonify(response)

@app.route("/unlockPassword", methods=["GET", "POST"])
def unlockPasssword():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    passHashRecived = dict_data['md5']

    if passHashRecived == '81dc9bdb52d04dc20036dbd8313ed055' : #1234
        jsonVal = {
        "action":"unlock"
        }
        r = requests.post(IP_ADDRESS+"/unlockPi", json=json.dumps(jsonVal))
        response = {
        "text":"correct password"
        }
        LOCKED_FLAG = 0
    else :
        response = {
        "text":"wrong password"
        }


    return jsonify(response)

@app.route("/lockPassword", methods=["GET", "POST"])
def lockPasssword():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    passHashRecived = dict_data['md5']

    if passHashRecived == '81dc9bdb52d04dc20036dbd8313ed055' : #1234
        jsonVal = {
        "action":"lock"
        }
        r = requests.post(IP_ADDRESS+"/lockPi", json=json.dumps(jsonVal))
        response = {
        "text":"correct password"
        }
        LOCKED_FLAG = 1
    else :
        response = {
        "text":"wrong password"
        }

    return jsonify(response)

@app.route("/addPersonPassword", methods=["GET", "POST"])
def addPersonPassword():
    json_data = request.get_json()
    print(type(json_data))
    dict_data = json.loads(json.dumps(json_data))

    print(dict_data)

    passHashRecived = dict_data['md5']

    if passHashRecived == '81dc9bdb52d04dc20036dbd8313ed055' : #1234
        response = {
        "text":"correct password"
        }
    else :
        response = {
        "text":"wrong password"
        }

    return jsonify(response)

@app.route("/android", methods=["GET", "POST"])
def android():
    json_data = request.get_json()

    response = {
        "code":100     
        }
    return jsonify(response)

if __name__ == "__main__":
    app.debug = True
    app.run()