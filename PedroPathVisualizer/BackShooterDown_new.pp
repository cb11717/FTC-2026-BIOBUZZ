{
  "startPoint": {
    "x": 59,
    "y": 132,
    "locked": false,
    "headingDeg": 90
  },
  "lines": [
    {
      "id": "line-muslny3d-z7yjut",
      "color": "#ffc516",
      "name": "Path 1",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 2000,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 59,
        "y": 132
      },
      "controlPoints": [],
      "heading": {
        "type": "linear",
        "startDeg": 90,
        "endDeg": 90
      }
    },
    {
      "id": "line-mvfxstot-dtxawr",
      "color": "#D976AC",
      "name": "",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 2000,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 53,
        "y": 130
      },
      "controlPoints": [
        {
          "x": 58,
          "y": 109
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 90,
        "endDeg": 120
      }
    },
    {
      "id": "line-mvfy0a8a-q4p6lz",
      "color": "#BA9D5A",
      "name": "",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 2000,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 59,
        "y": 132
      },
      "controlPoints": [
        {
          "x": 58,
          "y": 109
        }
      ],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": 120,
        "endDeg": 90
      }
    },
    {
      "id": "line-mvfy2ygz-lwbysy",
      "color": "#6DB8DC",
      "name": "",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 9,
        "y": 106
      },
      "controlPoints": [
        {
          "x": 60,
          "y": 109
        }
      ],
      "heading": {
        "type": "constant",
        "reverse": true,
        "degrees": 90
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
      "lineId": "line-muslny3d-z7yjut"
    },
    {
      "kind": "path",
      "lineId": "line-mvfxstot-dtxawr"
    },
    {
      "kind": "path",
      "lineId": "line-mvfy0a8a-q4p6lz"
    },
    {
      "kind": "path",
      "lineId": "line-mvfy2ygz-lwbysy"
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
    "curveThroughMaxPoints": 4,
    "experimentalFeatures": {
      "optimize": false,
      "curveThrough": false
    }
  },
  "version": "1.5.0",
  "timestamp": "2026-10-20T23:42:27.986Z"
}