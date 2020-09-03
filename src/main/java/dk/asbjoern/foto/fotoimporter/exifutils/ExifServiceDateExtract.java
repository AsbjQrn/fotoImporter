package dk.asbjoern.foto.fotoimporter.exifutils;

import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;


@Service
class ExifServiceDateExtract implements ExifService {


    public Optional<LocalDate> readExifDate(Optional<Metadata> metadata) {


        if (!metadata.isPresent()) {
            return Optional.ofNullable(null);
        }

        Date date = null;

        try {

            Collection<ExifIFD0Directory> list1 = Objects.requireNonNull(metadata.get().getDirectoriesOfType(ExifIFD0Directory.class));
            Collection<ExifSubIFDDirectory> list2 = Objects.requireNonNull(metadata.get().getDirectoriesOfType(ExifSubIFDDirectory.class));
            if (list1.size() > 1)
                throw new Exception("\"Der er mere en eet ExifIFD0Directory i filen \" +  file");


            for (ExifIFD0Directory exifIFD0Directory : list1) {
                date = exifIFD0Directory.getDate(306);
            }

            if (date == null) {
                for (ExifSubIFDDirectory exifSubIFDDirectory : list2) {
                    date = exifSubIFDDirectory.getDate(306);
                    if (date == null) {
                        date = exifSubIFDDirectory.getDateDigitized();
                    }
                    if (date == null) {
                        date = exifSubIFDDirectory.getDateOriginal();
                    }
                }
            }


        } catch (ImageProcessingException | IOException e) {
            e.printStackTrace();
        } catch (Exception n) {
            n.printStackTrace();
        }

        return date == null ? Optional.ofNullable(null) : Optional.of(convertToLocalDateViaInstant(date));

    }


    public LocalDate convertToLocalDateViaInstant(Date dateToConvert) {
        return dateToConvert.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }

}
