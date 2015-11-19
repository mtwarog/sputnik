package pl.touk.sputnik.connector.gerrit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import pl.touk.sputnik.configuration.Configuration;
import pl.touk.sputnik.configuration.ConfigurationSetup;
import pl.touk.sputnik.connector.ConnectorFacade;
import pl.touk.sputnik.connector.ConnectorFacadeFactory;
import pl.touk.sputnik.connector.ConnectorType;
import pl.touk.sputnik.review.ReviewFile;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.Resources;
import com.google.gerrit.extensions.api.GerritApi;
import com.google.gerrit.extensions.api.changes.ChangeApi;
import com.google.gerrit.extensions.api.changes.Changes;
import com.google.gerrit.extensions.api.changes.RevisionApi;
import com.google.gerrit.extensions.common.FileInfo;
import com.google.gerrit.extensions.restapi.RestApiException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.urswolfer.gerrit.client.rest.http.changes.FileInfoParser;

@RunWith(MockitoJUnitRunner.class)
public class GerritFacadeTest {

    @Mock
    private GerritApi gerritApi;

    @Test
    public void shouldNotAllowCommentOnlyChangedLines() {
        // given
        Configuration config = new ConfigurationSetup().setUp(ImmutableMap.of("cli.changeId", "abc", "cli.revisionId", "def",
                "global.commentOnlyChangedLines", Boolean.toString(true)));

        ConnectorFacadeFactory connectionFacade = new ConnectorFacadeFactory();

        // when
        ConnectorFacade gerritFacade = connectionFacade.build(ConnectorType.GERRIT, config);
        gerritFacade.validate(config);

        // then Nothing
    }

    @Test
    public void shouldParseListFilesResponse() throws IOException, RestApiException {
        List<ReviewFile> reviewFiles = createGerritFacade().listFiles();
        assertThat(reviewFiles).isNotEmpty();
    }

    @Test
    public void shouldNotListDeletedFiles() throws IOException, RestApiException {
        List<ReviewFile> reviewFiles = createGerritFacade().listFiles();
        assertThat(reviewFiles).hasSize(1);
    }

    private GerritFacade createGerritFacade() throws IOException, RestApiException {
        String listFilesJson = Resources.toString(Resources.getResource("json/gerrit-listfiles.json"), Charsets.UTF_8);
        JsonElement jsonElement = new JsonParser().parse(listFilesJson);
        Map<String, FileInfo> fileInfoMap = new FileInfoParser(new Gson()).parseFileInfos(jsonElement);

        Changes changes = mock(Changes.class);
        when(gerritApi.changes()).thenReturn(changes);
        ChangeApi changeApi = mock(ChangeApi.class);
        when(changes.id("changeId")).thenReturn(changeApi);
        RevisionApi revisionApi = mock(RevisionApi.class);
        when(changeApi.revision("revisionId")).thenReturn(revisionApi);
        when(revisionApi.files()).thenReturn(fileInfoMap);
        return new GerritFacade(gerritApi, new GerritPatchset("changeId", "revisionId"), mock(CommentFilter.class));
    }

}