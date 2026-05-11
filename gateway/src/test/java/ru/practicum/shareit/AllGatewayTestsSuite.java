package ru.practicum.shareit;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("Все тесты Gateway ShareIt")
@IncludeEngines("junit-jupiter")
@SelectPackages({
        "ru.practicum.shareit.user",
        "ru.practicum.shareit.item",
        "ru.practicum.shareit.booking",
        "ru.practicum.shareit.request"
})
public class AllGatewayTestsSuite {
}