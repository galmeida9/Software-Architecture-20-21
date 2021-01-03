package pt.ulisboa.tecnico.socialsoftware.tutor.tournament;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.ulisboa.tecnico.socialsoftware.tutor.config.DateHandler;
import pt.ulisboa.tecnico.socialsoftware.tutor.statement.dto.StatementQuizDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.tournament.dto.TournamentDto;
import pt.ulisboa.tecnico.socialsoftware.tutor.user.domain.User;

import javax.validation.Valid;
import java.security.Principal;
import java.util.List;
import java.util.Set;

@RestController
public class TournamentController {

    @Autowired
    private TournamentService tournamentService;

    @GetMapping(value = "/tournaments/{executionId}/getTournaments")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/getOpenTournaments")
    @PreAuthorize("hasRole('ROLE_STUDENT') and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getOpenedTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getOpenedTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/getClosedTournaments")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public List<TournamentDto> getClosedTournamentsForCourseExecution(@PathVariable int executionId) {
        return tournamentService.getClosedTournamentsForCourseExecution(executionId);
    }

    @GetMapping(value = "/tournaments/{executionId}/tournament/{tournamentId}")
    @PreAuthorize("(hasRole('ROLE_TEACHER') or hasRole('ROLE_STUDENT')) and hasPermission(#executionId, 'EXECUTION.ACCESS')")
    public TournamentDto getTournament(@PathVariable int executionId, @PathVariable Integer tournamentId) {
        return tournamentService.getTournament(tournamentId);
    }

}