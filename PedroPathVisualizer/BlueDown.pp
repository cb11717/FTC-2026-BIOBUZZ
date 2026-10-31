{
  "version": "2.0.0",
  "timestamp": "2026-10-31T14:54:19.194Z",
  "startPoint": {
    "x": 84,
    "y": 8,
    "locked": false,
    "headingDeg": 90
  },
  "lines": [
    {
      "id": "line-mvmrehg4-jfyq4p",
      "color": "#ffc516",
      "name": "flower",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 95,
        "y": 10
      },
      "controlPoints": [
        {
          "x": 88,
          "y": 40
        }
      ],
      "heading": {
        "type": "linear",
        "startDeg": -90,
        "endDeg": -80
      }
    },
    {
      "id": "line-mvmsg9sk-58caz3",
      "color": "#8A66C8",
      "name": "ShootPollen",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 84,
        "y": 8
      },
      "controlPoints": [
        {
          "x": 88,
          "y": 40
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": -80,
        "endDeg": -90
      }
    },
    {
      "id": "line-mvmsiofw-qo2h9t",
      "color": "#A96988",
      "name": "Park",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 133,
        "y": 26
      },
      "controlPoints": [
        {
          "x": 80,
          "y": 30
        },
        {
          "x": 100,
          "y": 30
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": -90,
        "endDeg": -90
      }
    }
  ],
  "shapes": [
    {
      "id": "triangle-1",
      "name": "Red Goal",
      "vertices": [
        {
          "x": 141.5,
          "y": 70
        },
        {
          "x": 141.5,
          "y": 141.5
        },
        {
          "x": 118.3,
          "y": 141.5
        },
        {
          "x": 135.5,
          "y": 118
        },
        {
          "x": 136.3,
          "y": 70.2
        }
      ],
      "color": "#dc2626",
      "fillColor": "#ff6b6b"
    },
    {
      "id": "triangle-2",
      "name": "Blue Goal",
      "vertices": [
        {
          "x": 6.2,
          "y": 116.9
        },
        {
          "x": 25,
          "y": 141.5
        },
        {
          "x": 0,
          "y": 141.5
        },
        {
          "x": 0,
          "y": 70
        },
        {
          "x": 6,
          "y": 70
        }
      ],
      "color": "#2563eb",
      "fillColor": "#60a5fa"
    }
  ],
  "sequence": [
    {
      "kind": "path",
      "lineId": "line-mvmrehg4-jfyq4p"
    },
    {
      "kind": "path",
      "lineId": "line-mvmsg9sk-58caz3"
    },
    {
      "kind": "path",
      "lineId": "line-mvmsiofw-qo2h9t"
    }
  ],
  "fieldPoints": [],
  "robot": {
    "forwardVelocity": 75,
    "strafeVelocity": 65,
    "aVelocity": 3.141592653589793,
    "kFriction": 0.1,
    "rLength": 18,
    "rWidth": 16.77,
    "safetyMargin": 1,
    "maxAcceleration": 30,
    "maxDeceleration": 30
  },
  "activePaths": []
}