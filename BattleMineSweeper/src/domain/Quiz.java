package domain;

import domain.value.QUIZDESCRIPTION;
import domain.value.QUIZID;

public class Quiz {

	private QUIZID QUIZID;
	private QUIZDESCRIPTION DESCRIPTION;
	private AnswerEnum ANSWER;

	// GETTER-------------------------------------
	public QUIZID getQUIZID() {
		return QUIZID;
	}

	public QUIZDESCRIPTION getDESCRIPTION() {
		return DESCRIPTION;
	}

	public AnswerEnum getANSWER() {
		return ANSWER;
	}

	// SETTER-------------------------------------
	public void setQUIZID(QUIZID qUIZID) {
		QUIZID = qUIZID;
	}

	public void setDESCRIPTION(QUIZDESCRIPTION dESCRIPTION) {
		DESCRIPTION = dESCRIPTION;
	}

	public void setANSWER(AnswerEnum aNSWER) {
		ANSWER = aNSWER;
	}
}
