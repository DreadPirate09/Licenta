#import RPI.GPIO as GPIO
from flask import Flask, jsonify, request, render_template
from time import sleep

# GPIO.setwarnings(False)
# GPIO.setmode(GPIO.BCM)
# GPIO.setup(18, GPIO.OUT)

app = Flask(__name__)

# GPIO.output(18, 1) seteaza releul pe on , da drumul la curent
# GPIO.output(18, 0) seteaza releul pe off , intrerupe flowul curentului

def turnOn():
    # GPIO.output(18, 1)
    print('output high')

def turnOnDelay(delay):
    # GPIO.output(18, 1)
    print('output high')

def turnOff():
    # GPIO.output(18, 0)
    print('output low')

def turnOffDelay(delay):
    # GPIO.output(18, 0)
    print('output low')


@app.route("/unlockPi", methods=["GET", "POST"])
def unlock():
    turnOn()
    # json_data = request.get_json() #Get the POSTed json
    # print(type(json_data))
    # dict_data = json.loads(json.dumps(json_data)) #Convert json to dictionary

    # print(dict_data)
    response = {
        "unlock":True   
        }

    print('ok , unlock')
    return jsonify(response)

@app.route("/lockPi", methods=["GET", "POST"])
def lock():
    turnOff()
    response = {
        "lock":True   
        }

    print('ok , lock')
    return jsonify(response)


if __name__ == "__main__":
    app.run(host='0.0.0.0', port=4040)

