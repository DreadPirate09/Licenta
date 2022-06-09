import cv2
import faces
import time

cap = cv2.VideoCapture(0)

while(cap.isOpened()):
    ret, frame = cap.read()
    cv2.imshow("frame", frame)

     
    # This condition prevents from infinite looping
    # incase video ends.
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
     
    # Save Frame by Frame into disk using imwrite method
    time.sleep(0.3)
    faces.execute(frame)

cv2.imwrite('frame.jpg',frame)
cap.release()
