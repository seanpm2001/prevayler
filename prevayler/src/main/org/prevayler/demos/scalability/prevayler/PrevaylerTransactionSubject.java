package org.prevayler.demos.scalability.prevayler;

import org.prevayler.PrevaylerFactory;
import org.prevayler.foundation.serialization.Serializer;

import java.io.File;
import java.io.PrintStream;
//import org.prevayler.implementation.PrevalenceTest;

public class PrevaylerTransactionSubject extends PrevaylerScalabilitySubject {
	private String _journalDirectory;

	public PrevaylerTransactionSubject(String journalDirectory, String journalSerializer) throws java.io.IOException,
			ClassNotFoundException, IllegalAccessException, InstantiationException {
		if (new File(journalDirectory).exists()) PrevalenceTest.delete(journalDirectory);
		PrevaylerFactory factory = new PrevaylerFactory();
		factory.configurePrevalentSystem(new TransactionSystem());
		factory.configurePrevalenceDirectory(journalDirectory);
		factory.configureJournalSerializer((Serializer) Class.forName(journalSerializer).newInstance());
		prevayler = factory.create();  //No snapshot is generated by the test.

		_journalDirectory = journalDirectory;
	}


	public Object createTestConnection() {
		return new PrevaylerTransactionConnection(prevayler);
	}

	public void reportResourcesUsed(PrintStream out) {
		int totalSize = 0;
		File[] files = new File(_journalDirectory).listFiles();
		for (int i = 0; i < files.length; i++) {
			totalSize += files[i].length();
		}
		out.println("Disk space used: " + totalSize);
	}

}
