package com.demo.testing.service.impl;

import com.demo.testing.exception.StudentNotFoundException;
import com.demo.testing.utils.ValidationUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.internal.WhiteboxImpl;

@RunWith(PowerMockRunner.class) // Включаем повермок
@PrepareForTest({ValidationUtils.class, StudentServiceImpl.class}) // указать все классы с приватными/статическими методами, которые будут вызыватьс методами теста
public class StudentServiceImplTest {

    private StudentServiceImpl studentService;

    @Before
    public void setUp() {
        /*
        * spy используется, когда тестируются публичные методы, которые вызывают приватые методы, и приватные методы мокаются
        * */
        studentService = PowerMockito.spy(new StudentServiceImpl());
        //we have to mock the static class whose method will behave on the way
        //in which we have defined in tests.
        PowerMockito.mockStatic(ValidationUtils.class);
    }

    @Test
    public void testGreetStudentForValidId() throws Exception {
        //init
        Integer i = 1;
        String expectedResult = "Hello Bikram";
        PowerMockito.when(ValidationUtils.validateId(i))
                .thenReturn(true);

        //we cannot simple call private methods by class.methodName in spy classes so
        //we have to provide method as String argument.
        PowerMockito.when(studentService, "getStudentName", i)
                .thenReturn("Bikram");

        //execute
        String actualResult = studentService.greetStudent(i);

        //assert
        Assert.assertEquals(expectedResult, actualResult);
    }

    /**
     * While writing unit test for a particular method the method call should be
     * limited within the method body. So no other methods should be called.
     *
     * @throws Exception when method/Student not found
     */
    @Test(expected = StudentNotFoundException.class)
    public void testGreetStudentForInvalidId() throws Exception {

        Integer i = 3;
        //ValidationUtils.validateId(i) should not be called since it is mocked
        PowerMockito.when(ValidationUtils.validateId(i))
                .thenReturn(false);

        //getStudentName method should not be called because of mocking
        PowerMockito.when(studentService, "getStudentName", i)
                .thenReturn("Bikram");
        //test will be passed if the calling method throws StudentNotFoundException
        studentService.greetStudent(i);
    }

    @Test
    public void testGetStudentName() throws Exception {
        //to call the private method we should use WhiteboxImpl or related class.
        Assert.assertEquals("Bikram", WhiteboxImpl.invokeMethod(studentService,
                "getStudentName", 1));
    }

}