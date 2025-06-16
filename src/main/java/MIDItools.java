 public class MIDItools {

     public static byte getNote(char note, int octave, boolean sharp) {
         int base = switch (Character.toUpperCase(note)) {
             case 'C' -> 0;
             case 'D' -> 2;
             case 'E' -> 4;
             case 'F' -> 5;
             case 'G' -> 7;
             case 'A' -> 9;
             case 'B' -> 11;
             default -> -1;
         };

         if (base == -1) return 0;

         int value = (octave * 12) + base + (sharp ? 1 : 0);
         if (value < 0 || value > 127) return 0;  // ← zusätzliche Prüfung hier !!!!! Vergistt du immer diese Frau Orhan!!!

         return (byte) value;
     }


     public static byte[] getHeader(byte speed) {
            return new byte[]{
                    0x4D, 0x54, 0x68, 0x64,
                    0x00, 0x00, 0x00, 0x06,
                    0x00, 0x00,
                    0x00, 0x01,
                    0x00, speed
            };
        }

        public static byte[] getNoteEvent(byte delay, boolean noteOn, byte note, byte velocity) {
            byte status = (byte) (noteOn ? 0b10010000 : 0b10000000);
            return new byte[]{delay, status, note, velocity};
        }

        public static byte[] addNoteToTrack(byte[] trackdata, byte[] noteEvent) {
            byte[] result = new byte[trackdata.length + noteEvent.length];
            System.arraycopy(trackdata, 0, result, 0, trackdata.length);
            System.arraycopy(noteEvent, 0, result, trackdata.length, noteEvent.length);
            return result;
        }

        public static byte[] getTrack(byte instrument, byte[] trackdata) {
            byte[] timing = new byte[]{
                    0x00, (byte) 0xFF, 0x58, 0x04, 0x04, 0x02, 0x18, 0x08,
                    0x00, (byte) 0xFF, 0x51, 0x03, 0x07, (byte) 0xA1, 0x20
            };

            byte[] setInstrument = new byte[]{
                    0x00, (byte) 0xC0, instrument
            };

            byte[] end = new byte[]{
                    (byte) 0xFF, 0x2F, 0x00
            };

            int contentLength = timing.length + setInstrument.length + trackdata.length;
            byte[] header = new byte[8];
            header[0] = 0x4D;
            header[1] = 0x54;
            header[2] = 0x72;
            header[3] = 0x6B;
            header[4] = (byte) ((contentLength >> 24) & 0xFF);
            header[5] = (byte) ((contentLength >> 16) & 0xFF);
            header[6] = (byte) ((contentLength >> 8) & 0xFF);
            header[7] = (byte) (contentLength & 0xFF);

            byte[] result = new byte[header.length + contentLength + end.length];
            int pos = 0;
            for (byte b : header) result[pos++] = b;
            for (byte b : timing) result[pos++] = b;
            for (byte b : setInstrument) result[pos++] = b;
            for (byte b : trackdata) result[pos++] = b;
            for (byte b : end) result[pos++] = b;

            return result;
        }
    }
