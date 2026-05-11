package ru.practicum.shareit;

import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;
import org.springframework.test.context.ActiveProfiles;

@Suite
@SuiteDisplayName("Все тесты Server ShareIt")
@IncludeEngines("junit-jupiter")
@SelectPackages({
        "ru.practicum.shareit.user",
        "ru.practicum.shareit.item",
        "ru.practicum.shareit.booking",
        "ru.practicum.shareit.request"
})
@ActiveProfiles("test")
public class AllServerTestsSuite {
}