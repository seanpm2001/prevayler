//Prevayler(TM) - The Free-Software Prevalence Layer.
//Copyright (C) 2001-2003 Klaus Wuestefeld
//This library is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

package org.prevayler.implementation;

import java.io.IOException;

import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

public class LogFileRollingTest extends PrevalenceTest {

	private Prevayler _prevayler;
	private String _prevalenceBase;

	public void testFileRolling() throws Exception {

		crashRecover(""); //There is nothing to recover at first. A new system will be created.

		append("a","a");
		crashRecover("a");

		append("b","ab");
		append("c","abc");
		append("d","abcd"); //Starts new file.
		append("e","abcde");
		append("f","abcdef"); //Starts new file.
		crashRecover("abcdef");
		
		append("g","abcdefg");
		snapshot();
		append("h","abcdefgh");
		append("i","abcdefghi");
		append("j","abcdefghij"); //Starts new file.
		crashRecover("abcdefghij");

		_prevayler.close();
		deleteFromTestDirectory("0000000000000000001.transactionLog");
		deleteFromTestDirectory("0000000000000000002.transactionLog");
		deleteFromTestDirectory("0000000000000000004.transactionLog");
		deleteFromTestDirectory("0000000000000000006.transactionLog");
		deleteFromTestDirectory("0000000000000000007.transactionLog");
		deleteFromTestDirectory("0000000000000000008.transactionLog");
		deleteFromTestDirectory("0000000000000000010.transactionLog");
	}

	private void crashRecover(String expectedResult) throws Exception {
		out("CrashRecovery.");
		if (_prevayler != null) _prevayler.close();
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configureTransactionLogFileSizeThreshold(1);  //Find out what the size is.
		factory.configureTransactionLogFileAgeThreshold(0);  //Not being tested.
		factory.configurePrevalenceBase(_testDirectory);
		factory.configurePrevalentSystem(new AppendingSystem());
		_prevayler = factory.create();
		verify(expectedResult);
	}

	private void snapshot() throws IOException {
		out("Snapshot.");
		_prevayler.takeSnapshot();
	}


	private void append(String appendix, String expectedResult) throws Exception {
		out("Appending " + appendix);
		_prevayler.execute(new Appendix(appendix));
		verify(expectedResult);
	}


	private void verify(String expectedResult) {
		out("Expecting result: " + expectedResult);
		assertEquals(expectedResult, system().value());
	}


	private AppendingSystem system() {
		return (AppendingSystem)_prevayler.prevalentSystem();
	}

	private static void out(Object obj) {
		if (false) System.out.println(obj);   //Change this line to see what the test is doing.
	}

}
