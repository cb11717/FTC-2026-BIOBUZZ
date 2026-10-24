{
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
        "x": 11.7,
        "y": 55
      },
      "controlPoints": [],
      "heading": {
        "type": "linear",
        "reverse": false,
        "startDeg": -90,
        "endDeg": -160,
        "degrees": 0
      }
    },
    {
      "kind": "atomic",
      "id": "line-mvfvqwfg-yntw64",
      "endPoint": {
        "x": 56,
        "y": 9
      },
      "controlPoints": [],
      "heading": {
        "type": "linear",
        "reverse": true,
        "startDeg": -160,
        "endDeg": -90
      },
      "color": "#D8856B",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "name": "shootingAfterTip"
    },
    {
      "id": "line-muna5e3y-w62pxc",
      "color": "#C6798D",
      "name": "liningUp",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 0,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 9,
        "y": 29
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
    },
    {
      "id": "line-mune6oc5-njez9w",
      "color": "#86B88D",
      "name": "collectingWallPollen",
      "locked": false,
      "waitBeforeMs": 0,
      "waitAfterMs": 1000,
      "waitBeforeName": "",
      "waitAfterName": "",
      "kind": "atomic",
      "endPoint": {
        "x": 9,
        "y": 9
      },
      "controlPoints": [],
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
      "waitAfterMs": 2000,
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
      "lineId": "line-muna5e3y-w62pxc"
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
  "timestamp": "2026-10-19T23:29:11.709Z"
}