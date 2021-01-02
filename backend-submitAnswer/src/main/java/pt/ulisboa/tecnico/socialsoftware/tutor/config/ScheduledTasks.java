package pt.ulisboa.tecnico.socialsoftware.tutor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.StatementService;

@Component
public class ScheduledTasks {

	@Autowired
	private StatementService statementService;
}