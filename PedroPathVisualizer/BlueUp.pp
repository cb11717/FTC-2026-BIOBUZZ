{
  "startPoint": {
    "x": 80,
    "y": 136,
    "name": "start",
    "locked": false,
    "headingDeg": 90
  },
  "lines": [
    {
      "id": "line-mvmop7so-0hhae8",
      "color": "#ffc516",
      "name": "flower",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 130,
        "y": 90,
        "locked": false
      },
      "controlPoints": [
        {
          "x": 120,
          "y": 80
        }
      ],
      "heading": {
        "type": "linear",
        "startDeg": 90,
        "endDeg": 20
      }
    },
    {
      "id": "line-mvmpk3ue-u4cvcx",
      "color": "#C5BA88",
      "name": "shootPollen1",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 80,
        "y": 136
      },
      "controlPoints": [
        {
          "x": 120,
          "y": 80
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 20,
        "endDeg": 90
      }
    },
    {
      "id": "line-mvmq6gix-1a2vd4",
      "color": "#55CCC5",
      "name": "CollectingWallPollen",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 135,
        "y": 136
      },
      "controlPoints": [
        {
          "x": 125,
          "y": 100
        },
        {
          "x": 135,
          "y": 120
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 90,
        "endDeg": 90
      }
    },
    {
      "kind": "atomic",
      "id": "line-mvmqwhix-c29xn8",
      "endPoint": {
        "x": 80,
        "y": 136
      },
      "controlPoints": [
        {
          "x": 100,
          "y": 110
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 90,
        "endDeg": 90
      },
      "color": "#D8DD99",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "name": "ShootingLast4"
    },
    {
      "kind": "atomic",
      "id": "line-mvmqzxhe-si9h32",
      "endPoint": {
        "x": 133,
        "y": 48
      },
      "controlPoints": [
        {
          "x": 140,
          "y": 50
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 90,
        "endDeg": 90
      },
      "color": "#CBA95B",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "name": "Park"
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
      "lineId": "line-mvmop7so-0hhae8"
    },
    {
      "kind": "path",
      "lineId": "line-mvmpk3ue-u4cvcx"
    },
    {
      "kind": "path",
      "lineId": "line-mvmq6gix-1a2vd4"
    },
    {
      "kind": "path",
      "lineId": "line-mvmqwhix-c29xn8"
    },
    {
      "kind": "path",
      "lineId": "line-mvmqzxhe-si9h32"
    }
  ],
  "fieldPoints": [],
  "activePaths": [],
  "settings": {
    "xVelocity": 75,
    "yVelocity": 65,
    "aVelocity": 3.141592653589793,
    "kFriction": 0.1,
    "rWidth": 17.5,
    "rHeight": 16.5,
    "safetyMargin": 1,
    "maxVelocity": 40,
    "maxAcceleration": 30,
    "maxDeceleration": 30,
    "fieldMap": "biobuzz.webp",
    "robotImage": "/robot.png",
    "showGhostPaths": false,
    "showOnionLayers": false,
    "onionLayerSpacing": 3,
    "onionColor": "#dc2626",
    "onionNextPointOnly": false,
    "showHeadingArrow": false,
    "showCurrentTValue": false,
    "leftPanelWidth": 242,
    "rightPanelWidth": 620,
    "headingArrowLength": 50,
    "headingArrowColor": "#ffffff",
    "headingArrowThickness": 2,
    "pathOpacity": 1,
    "leftPanelMinWidth": 0,
    "rightPanelMinWidth": 0,
    "penToolMaxPaths": 8,
    "experimentalFeatures": {
      "optimize": false,
      "curveThrough": false
    },
    "curveThroughMaxPoints": 4
  },
  "version": "1.5.0",
  "timestamp": "2026-10-24T18:48:36.590Z"
}