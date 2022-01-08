package si.fri.rsoteam.lib.dtos;

import java.time.Instant;
import java.util.List;

public class VideoDto {
    public Integer id;
    public String link;
    public String description;
    public List<TagDto> tags;
    public Instant createdAt;

    public VideoDto() {
    }
}
