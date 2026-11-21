# PART A – DESIGN AND PSEUDOCODE

## A1 File format
Text record: `passId|regNo|name|routeNo|boardingStop|validityDate|amountPaid`. Escape a literal `|` and backslash or use a proper CSV/TSV parser. Binary `DataOutputStream` order: writeUTF(passId), writeUTF(regNo), writeUTF(name), writeInt(routeNo), writeUTF(boardingStop), writeUTF(ISO date), writeDouble(amount). Reader must use the same order/types.

**ADD_PASS:** validate fields; open UTF-8 writer in append mode wrapped by BufferedWriter; encode fields; write one record/newline; flush/close; report I/O error if write fails.

**FIND_PASS:** if file missing report missing; open BufferedReader; read line by line; parse escaped fields; skip/log invalid field count or invalid numeric/date fields; return matching pass ID; otherwise return NOT_FOUND.

**LIST_BY_ROUTE:** open BufferedReader; parse each row; skip/log damaged rows; append valid rows whose route equals requested route; return list.

Damaged line: validate field count and field types, skip/log and continue. Missing file: distinguish from empty file; create only for add. Incomplete write: use temp file + atomic replace for rewrites; for append logs use length/checksum/commit marker and ignore incomplete final record.

Alternative RandomAccessFile: fixed record i at offset i×recordSize; seek is O(1), but lookup by ID still O(n) without an index. Fixed-width padding wastes space.

## A2 Buffered I/O
50 MiB = 52,428,800 bytes; one-byte loop makes about 52.4 million Java read calls (plus EOF). Buffered 64 KiB reads require about 800 block reads. Pseudocode: open BufferedInputStream; read blocks; count newline bytes; count final unterminated line; close. Buffering gives little benefit for tiny files, already-blocked I/O or CPU-bound work. `Files.readAllLines()` is O(n) time and O(n) memory; streaming is O(n) time and O(B) buffer memory.

## A3 DAO
Repeated steps: connection, SQL preparation, parameter binding, execution, ResultSet-to-object mapping, exception handling and resource closing. Variable parts: table, columns, key and mapper. Generic `Dao<T,K>` has insert, findById, findAll, update, delete. Student mapping: `Student.id → student.student_id` (primary key), name → name, email → email. DAO localizes schema changes; direct SQL in UI/service spreads changes across classes. Runtime asymptotics per DB call are broadly the same.

## A4 Serialization
Persist stable domain data (hostel, rooms, resident name/register number). Mark password and live JDBC Connection `transient`; never serialize credentials or live connections. Serialization traverses reachable non-transient objects; every such object must be Serializable. Add explicit `serialVersionUID`; an old stream missing a new phone field normally yields null/default if compatible; mismatched UID can cause InvalidClassException. CSV/JSON is portable and human-readable; serialization is Java-specific. Save/load n residents is O(n); text formats are often larger but easier to evolve when optional fields are handled.

## A5 Transactions and injection
Transfer pseudocode: validate amount; save auto-commit; set false; debit only if sufficient funds; verify one row; credit destination; verify one row; commit; on any failure rollback; restore auto-commit and close. Two auto-commit updates can lose/create money if failure occurs between them. Concatenated login SQL lets crafted input alter query syntax; use `PreparedStatement` placeholders and setString. In production store password hashes, not plaintext.
