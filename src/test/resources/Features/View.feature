# Author: Phuong Anh
# Date: 12/11/2024
# Description: View tool MeClip

@SmokeScenario
Feature: View tool MeClip

  @View
  Scenario Outline: Check view clip
    Given Runner tool main
    And Close browser and all tabs

    Examples:
    | second |
    | 3 |