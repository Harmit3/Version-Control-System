// imports used
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;


public class bug_commit_locate_bonus {

    private static class CommitInfo {
        int revisionNumber; 
        String hash;
        String message;

        CommitInfo(int rev, String hash, String msg) {
            this.revisionNumber = rev;
            this.hash = hash;
            this.message = msg;
        }
    }

    private static class LineChange {
        int old_line; 
        int new_line; 

        LineChange(int old_line, int new_line) {
            this.old_line = old_line;
            this.new_line = new_line;
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Invalid terminal command, must use like this. java BugIntroFixDetector <path-to-git-repo>");
            System.exit(1);
        }

        String repoPath = args[0];

        // load all commits
        List<CommitInfo> history = Get_commmit_history(repoPath);
        System.out.println("Commits found -> " + history.size());

        for (CommitInfo c : history) {
            // load commit id
            String shortHash = c.hash.length() >= 7 ? c.hash.substring(0, 7) : c.hash;
            System.out.println("--rev " + c.revisionNumber + " " + shortHash + "  msg=\"" + c.message + "\"");
        }
        System.out.println();

        Map<String, Integer> revByHash = new HashMap<>();
        Map<String, Integer> indexByHash = new HashMap<>();

        for (int i = 0; i < history.size(); i++) {
            CommitInfo c = history.get(i);
            revByHash.put(c.hash, c.revisionNumber);
            indexByHash.put(c.hash, i);
        }

        //nfind bug-introducing commit
        for (CommitInfo fixCommit : history) {
            if (!isBugFixCommit(fixCommit.message)) {
                continue;
            }

            String fixShort = fixCommit.hash.length() >= 7 ? fixCommit.hash.substring(0, 7) : fixCommit.hash;
            System.out.println("** Detected BUG-FIX commit: rev " + fixCommit.revisionNumber
                    + " (" + fixShort + ") \"" + fixCommit.message + "\"");

            String parentHash = getParentCommit(repoPath, fixCommit.hash);
            Integer parentIndex = indexByHash.get(parentHash);
            List<String> changedFiles = getChangedFiles(repoPath, parentHash, fixCommit.hash);

            for (String filePath : changedFiles) {
                List<String> old_lines = loadFileFromCommit(repoPath, parentHash, filePath);
                List<String> new_lines = loadFileFromCommit(repoPath, fixCommit.hash, filePath);
                List<LineChange> changes = findLineChanges(old_lines, new_lines);
                //System.out.println("   File " + filePath + " has " + changes.size() + " changed lines");

                for (LineChange change : changes) {
                    if (change.old_line <= 0 || change.old_line > old_lines.size()) {
                        continue;
                    }

                    String old_lineText = old_lines.get(change.old_line - 1);
                    String buggyCode = codeOnly(old_lineText);
                    if (buggyCode.isEmpty()) {
                        continue;
                    }

                    // find where line was introduced to get bug introducign commit
                    String introHash = find_introducing_commit_line(repoPath, history, parentIndex, filePath, change.old_line, buggyCode);

                    Integer introRev = revByHash.get(introHash);
                    int fixRev = fixCommit.revisionNumber;
                    int fixLine = (change.new_line > 0) ? change.new_line : change.old_line;
                    String introShort = introHash.length() >= 7 ? introHash.substring(0, 7) : introHash;
                
                    System.out.printf(
                        "Bug introduced in revision %d (%s), file %s, line %d; " +
                        "bug fixed in revision %d (%s), line %d%n",
                        introRev, introShort, filePath, change.old_line,
                        fixRev, fixShort, fixLine
                    );
                }}System.out.println();}}


    private static List<CommitInfo> Get_commmit_history(String repoPath) throws IOException, InterruptedException {

        // git log
        String output = runGit(repoPath,"log", "--reverse", "--pretty=format:%H:::%s");

        List<CommitInfo> commits = new ArrayList<>();
        if (output == null || output.isEmpty()) return commits;

        String[] lines = output.split("\n");
        int rev = 1;
        for (String line : lines) {
            int idx = line.indexOf(":::");
            if (idx <= 0) continue;
            String hash = line.substring(0, idx).trim();
            String msg = line.substring(idx + 3).trim();
            commits.add(new CommitInfo(rev++, hash, msg));
        }
        return commits;
    }

    // detect bug fix commit using keyword detection which is fix
    private static boolean isBugFixCommit(String msg) {
        if (msg == null) return false;
        String lower = msg.toLowerCase();
        return lower.contains("fix");
    }

    private static String getParentCommit(String repoPath, String commitHash) throws IOException, InterruptedException {
        String out = runGit(repoPath, "rev-list", "--parents", "-n", "1", commitHash);
        if (out == null || out.trim().isEmpty()) return null;
        String[] parts = out.trim().split("\\s+");
        if (parts.length < 2) return null;
        return parts[1]; 
    }

    private static List<String> getChangedFiles(String repoPath, String parentHash, String childHash) throws IOException, InterruptedException {
        String out = runGit(repoPath, "diff", "--name-only", parentHash, childHash);
        List<String> files = new ArrayList<>();
        if (out == null || out.trim().isEmpty()) return files;
        for (String line : out.split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) files.add(line);
        }
        return files;
    }

    // loading files from the commit
    private static List<String> loadFileFromCommit(String repoPath, String commitHash, String filePath) throws IOException, InterruptedException {
        String spec = commitHash + ":" + filePath;
        String out = runGit(repoPath, "show", spec);
        if (out == null || out.isEmpty()) return Collections.emptyList();
        return Arrays.asList(out.split("\n", -1)); 
    }

    // method to strip whitespace.etc
    private static String codeOnly(String line) {
        if (line == null) return "";
        int idx = line.indexOf("//");
        if (idx != -1) {
            line = line.substring(0, idx);
        }
        return line.trim();
    }

    // findLineChanges is a simplified version of the line tracking algorithm developed in the main part of the project
    private static List<LineChange> findLineChanges(List<String> old_lines, List<String> new_lines) {
        List<LineChange> result = new ArrayList<>();

        int min = Math.min(old_lines.size(), new_lines.size());
        for (int i = 0; i < min; i++) {
            String oldCode = codeOnly(old_lines.get(i));
            String newCode = codeOnly(new_lines.get(i));

            if (oldCode.isEmpty() && newCode.isEmpty()) {
                continue;
            }

            if (!Objects.equals(oldCode, newCode)) {
                result.add(new LineChange(i + 1, i + 1));
            }
        }

        for (int i = min; i < old_lines.size(); i++) {
            String oldCode = codeOnly(old_lines.get(i));
            if (!oldCode.isEmpty()) {
                result.add(new LineChange(i + 1, -1));
            }
        }

        return result;
    }

    // Earliest introduction search
    private static String find_introducing_commit_line(String repoPath, List<CommitInfo> history, int fromIndex, String filePath, int lineNumber, String buggyCode) throws IOException, InterruptedException {

        String intro_hash = null;
        boolean Match_found = false;

        for (int i = fromIndex; i >= 0; i--) {
            CommitInfo c = history.get(i);
            List<String> lines = loadFileFromCommit(repoPath, c.hash, filePath);

            if (lineNumber > lines.size()) {
                if (Match_found) {
                    break;
                } else {
                    continue;
                }
            }

            String candidateCode = codeOnly(lines.get(lineNumber - 1));
            if (Objects.equals(candidateCode, buggyCode)) {
                intro_hash = c.hash;
                Match_found = true;
            } else {
                if (Match_found) {
                    break;
                }
            }
        }
        return intro_hash;
    }

    // helper method to run git commands
    private static String runGit(String repoPath, String... args)
            throws IOException, InterruptedException {

        List<String> cmd = new ArrayList<>();
        cmd.add("git");
        cmd.addAll(Arrays.asList(args));

        ProcessBuilder pb = new ProcessBuilder(cmd);
        pb.directory(new File(repoPath));
        pb.redirectErrorStream(true);

        Process p = pb.start();
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }
        return sb.toString().trim();
    }
}
