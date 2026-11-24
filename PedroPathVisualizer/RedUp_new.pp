{
  "version": "2.0.0",
  "timestamp": "2026-11-24T01:51:36.401Z",
  "startPoint": {
    "x": 56,
    "y": 9,
    "locked": false,
    "headingDeg": 90
  },
  "lines": [
    {
      "id": "line-mun9p9lc-cjxls1",
      "color": "#797A5D",
      "name": "flower",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 15,
        "y": 50
      },
      "controlPoints": [],
      "heading": {
        "type": "linear",
        "reverse": false,
        "startDeg": -90,
        "endDeg": -180,
        "degrees": 0
      }
    },
    {
      "id": "line-mvfvqwfg-yntw64",
      "color": "#D8856B",
      "name": "shootingAfterTip",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 56,
        "y": 9
      },
      "controlPoints": [
        {
          "x": 39,
          "y": 38
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": -180,
        "endDeg": -90
      }
    },
    {
      "id": "line-mune6oc5-njez9w",
      "color": "#86B88D",
      "name": "collectingWallPollen",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 9,
        "y": 9
      },
      "controlPoints": [
        {
          "x": 4.4339364619932295,
          "y": 49.571152714874344
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": -90,
        "endDeg": -90
      }
    },
    {
      "id": "line-munae3wa-323lt1",
      "color": "#A667C6",
      "name": "collectingWallPollen",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 56,
        "y": 9
      },
      "controlPoints": [
        {
          "x": 11.8,
          "y": 48
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
      "lineId": "line-mun9p9lc-cjxls1"
    },
    {
      "kind": "path",
      "lineId": "line-mvfvqwfg-yntw64"
    },
    {
      "kind": "path",
      "lineId": "line-mune6oc5-njez9w"
    },
    {
      "kind": "path",
      "lineId": "line-munae3wa-323lt1"
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