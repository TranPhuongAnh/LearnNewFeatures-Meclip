# Author: Phuong Anh
# Date: 12/11/2024
# Description: View tool MeClip

@SmokeScenario
Feature: View tool MeClip

  @View
  Scenario Outline: Check view clip
    Given Run tool and video viewing time is <second>
    And Close browser

    Examples:
    | second |
    | 3 |