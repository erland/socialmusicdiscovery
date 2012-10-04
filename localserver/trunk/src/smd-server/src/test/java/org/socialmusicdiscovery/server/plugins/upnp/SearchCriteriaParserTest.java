package org.socialmusicdiscovery.server.plugins.upnp;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.antlr.runtime.*;
import org.socialmusicdiscovery.server.support.format.antlr.*;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.BeforeSuite;

public class SearchCriteriaParserTest {

	

  @Test(dataProvider = "dp")
  public void f(Integer n, String s) {
  }

  @Test
  public void basicParsing() {
		// Create an input character stream from standard in
		ANTLRInputStream input;
		ExprParser parser;
		try {
			//input = new ANTLRInputStream(System.in);
			input = new ANTLRInputStream(new  ByteArrayInputStream("((1+4)*(11*5+3)+n)\n".getBytes()));
		// Create an ExprLexer that feeds from that stream
		ExprLexer lexer = new ExprLexer(input);
		// Create a stream of tokens fed by the lexer
		CommonTokenStream tokens = new CommonTokenStream(lexer);
		// Create a parser that feeds off the token stream
		parser = new ExprParser(tokens);
		System.out.println("before prog");
		parser.prog();
		System.out.println("test");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RecognitionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
  }
  @BeforeMethod
  public void beforeMethod() {
  }


  @DataProvider
  public Object[][] dp() {
    return new Object[][] {
      new Object[] { 1, "a" },
      new Object[] { 2, "b" },
    };
  }

  @BeforeClass
  public void beforeClass() {
  }

  @BeforeTest
  public void beforeTest() {
	}

  @BeforeSuite
  public void beforeSuite() {
  }

}
