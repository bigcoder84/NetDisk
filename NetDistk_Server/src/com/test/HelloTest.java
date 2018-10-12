package com.test;

import java.util.Random;

import org.junit.Test;

public class HelloTest {
	@Test
	public void test() {
		Random random = new Random();
		int string = random.nextInt(100000);
		System.out.println(string);
	}
}
