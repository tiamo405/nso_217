"""Offline fixtures for W5.MailClient in mobile 4.1.1 (no server login)."""
import contextlib
import io
import socket
import struct
import unittest
import tempfile
from pathlib import Path
from unittest.mock import Mock, patch

from mail_client import NSOMailClient, NSOMessage, NSOReader, read_accounts, main, build_parser, process_account


def utf(value):
    data = value.encode('utf-8')
    return struct.pack('>H', len(data)) + data


class MailProtocolTest(unittest.TestCase):
    def setUp(self):
        self.client = NSOMailClient('unused', 0)
        self.client.sock = Mock()
        self.client.connected = True
        self.output = contextlib.redirect_stdout(io.StringIO())
        self.output.__enter__()
        self.addCleanup(self.output.__exit__, None, None, None)

    def test_mail_wire_actions(self):
        for action, mail_id, expected in [
            (0, None, bytes.fromhex('d8 0001 00')),
            (1, 12345, bytes.fromhex('d8 0005 01 00003039')),
            (2, 12345, bytes.fromhex('d8 0005 02 00003039')),
            (3, 12345, bytes.fromhex('d8 0005 03 00003039')),
        ]:
            self.client._send_mail_action(action, mail_id)
            self.client.sock.sendall.assert_called_with(expected)
        with self.assertRaises(ValueError):
            self.client._send_mail_action(3)

    def test_mobile_login_layout(self):
        self.client.recv_packet = Mock(return_value=(None, None))
        self.client.login('test-user', 'test-password')
        setup, login = [c.args[0] for c in self.client.sock.sendall.call_args_list]
        expected = (bytes.fromhex('83 04 01 01') + struct.pack('>ii', 480, 800)
                    + b'\x01\x01' + utf('Unity Mobile')
                    + bytes(10) + utf('0'))
        self.assertEqual(setup, b'\xe3' + struct.pack('>H', len(expected)) + expected)
        r = NSOReader(login[3:])
        self.assertEqual(r.read_byte(), -127)
        self.assertEqual([r.read_utf() for _ in range(5)],
                         ['test-user', 'test-password', '4.1.1', '', ''])
        self.assertEqual(len(r.read_utf()), 12)
        self.assertEqual(r.read_byte(), 0)
        self.assertEqual(r.remaining(), 0)

    def test_list_and_truncated_list(self):
        payload = (b'\x00\x01' + struct.pack('>i', 12345) + utf('Hệ thống')
                   + utf('Quà') + b'\x00\x01' + struct.pack('>qq', 100, 200) + b'\x01')
        mails = self.client._parse_mail_list(payload)
        self.assertEqual(mails, [dict(mail_id=12345, sender='Hệ thống', title='Quà',
                                     is_read=False, is_received=True, created_time=100,
                                     expired_time=200, has_attachments=True)])
        with self.assertRaises(EOFError):
            self.client._parse_mail_list(payload[:-1])

    def test_read_detail_has_no_success_byte(self):
        payload = (b'\x01' + struct.pack('>i', 12345) + utf('Hệ thống') + utf('Quà')
                   + utf('Nội dung') + struct.pack('>q', 200) + b'\x01'
                   + struct.pack('>iiiqBhi?', 10, 20, 30, 40, 1, 123, 5, True))
        detail = self.client._parse_mail_read(payload)
        self.assertEqual(detail['content'], 'Nội dung')
        self.assertEqual(detail['exp'], 40)
        self.assertEqual(detail['items'], [dict(template_id=123, quantity=5, is_lock=True)])
        self.assertEqual(self.client._parse_mail_action_result(payload), (1, 12345, True))
        # Delete response 5 bytes (action 1 byte + mail_id 4 bytes)
        self.assertEqual(self.client._parse_mail_action_result(bytes.fromhex('02 00003039')), (2, 12345, True))
        # Delete response 6 bytes (action 1 byte + mail_id 4 bytes + success)
        self.assertEqual(self.client._parse_mail_action_result(bytes.fromhex('02 00003039 01')), (2, 12345, True))
        self.assertEqual(self.client._parse_mail_action_result(bytes.fromhex('02 00003039 00')), (2, 12345, False))
        with self.assertRaises(EOFError):
            self.client._parse_mail_action_result(payload[:5])

    def test_wait_matches_action_and_mail_id(self):
        expected = bytes.fromhex('03 00003039 00')
        self.client.recv_packet = Mock(side_effect=[
            (12, b'chat'), (-40, bytes.fromhex('02 00003039 01')),
            (-40, bytes.fromhex('03 00003038 01')), (-40, expected)])
        self.assertEqual(self.client._wait_mail_packet(3, 12345), expected)
        self.assertEqual(self.client._parse_mail_action_result(expected), (3, 12345, False))

    def test_failed_claim_never_deletes_mail(self):
        for response in (None, bytes.fromhex('03 00000001 00'), b'\x03\x00'):
            with self.subTest(response=response):
                self.client.request_mail_list = Mock(return_value=[dict(
                    mail_id=1, is_read=True, is_received=False, has_attachments=True)])
                self.client._wait_mail_packet = Mock(return_value=response)
                self.client.delete_mail = Mock()
                self.client.receive_all_mail(delete_after_claim=True)
                self.client.delete_mail.assert_not_called()

    def test_default_bulk_claim_does_not_delete(self):
        self.client.request_mail_list = Mock(return_value=[dict(
            mail_id=1, is_read=True, is_received=False, has_attachments=True)])
        self.client._wait_mail_packet = Mock(return_value=bytes.fromhex('03 00000001 01'))
        self.client.delete_mail = Mock()
        self.client.receive_all_mail()
        self.client.delete_mail.assert_not_called()

    def test_encrypted_fragment_survives_timeout_at_every_boundary(self):
        first = bytes.fromhex('d8 0006 03 00003039 01')
        second = bytes.fromhex('e0 d8 00000002 00 00')
        key = b'\x21\x43\x65'
        plain = first + second
        wire = bytes(b ^ key[i % len(key)] for i, b in enumerate(plain))
        for cut in range(1, len(first)):
            with self.subTest(cut=cut):
                c = NSOMailClient('unused', 0)
                c.key = list(key)
                c.sock = Mock()
                c.sock.recv.side_effect = [wire[:cut], socket.timeout(), wire[cut:]]
                self.assertEqual(c.recv_packet(), (None, None))
                self.assertEqual(c.recv_packet(), (-40, first[3:]))
                self.assertEqual(c.recv_packet(), (-40, b'\x00\x00'))

    def test_claim_then_delete_and_verify(self):
        mail = dict(mail_id=1, is_read=True, is_received=False, has_attachments=True)
        self.client.request_mail_list = Mock(side_effect=[[mail], []])
        events = []
        self.client.claim_mail_attachment = Mock(side_effect=lambda mid: events.append(('claim', mid)))
        self.client.delete_mail = Mock(side_effect=lambda mid: events.append(('delete', mid)))
        self.client._wait_mail_packet = Mock(side_effect=[
            bytes.fromhex('03 00000001 01'), bytes.fromhex('02 00000001 01')])
        result = self.client.receive_all_mail(delete_after_claim=True)
        self.assertEqual(events, [('claim', 1), ('delete', 1)])
        self.assertEqual(result, dict(claimed=1, deleted=1, failed=0))

    def test_empty_mail_deleted_but_server_still_lists_it(self):
        mail = dict(mail_id=1, is_read=True, is_received=False, has_attachments=False)
        self.client.request_mail_list = Mock(return_value=[mail])
        self.client.claim_mail_attachment = Mock()
        self.client._wait_mail_packet = Mock(return_value=bytes.fromhex('02 00000001 01'))
        result = self.client.receive_all_mail(delete_after_claim=True)
        self.client.claim_mail_attachment.assert_not_called()
        self.assertEqual(result, dict(claimed=0, deleted=0, failed=1))

    def test_mail_list_timeout_is_not_empty_success(self):
        self.client._wait_mail_packet = Mock(return_value=None)
        with self.assertRaises(RuntimeError):
            self.client.receive_all_mail(delete_after_claim=True)

    def test_csv_batch_continues_after_failed_account_without_input(self):
        with tempfile.TemporaryDirectory() as folder:
            path = Path(folder) / 'accounts.csv'
            path.write_text('\ufeffusername,password\nfirst,pass1\n\nsecond,pass2\n', encoding='utf-8')
            self.assertEqual(read_accounts(path), [('first', 'pass1'), ('second', 'pass2')])
            with patch('mail_client.process_account') as process, patch('builtins.input') as prompt:
                process.side_effect = [dict(characters=0, claimed=0, deleted=0, failed=1),
                                       dict(characters=2, claimed=3, deleted=3, failed=0)]
                self.assertEqual(main([str(path), '--account-delay', '0']), 1)
                self.assertEqual([c.args[1] for c in process.call_args_list], ['first', 'second'])
                prompt.assert_not_called()

    def test_all_characters_reconnect_and_continue_after_select_failure(self):
        args = build_parser().parse_args(['--character-delay', '0'])
        first, second = Mock(), Mock()
        for c in (first, second):
            c.characters = ['char1', 'char2']
            c.connect.return_value = True
            c.login.return_value = True
        first.select_character.return_value = False
        second.select_character.return_value = True
        second.receive_all_mail.return_value = dict(claimed=2, deleted=2, failed=0)
        with patch('mail_client.NSOMailClient', side_effect=[first, second]):
            result = process_account(args, 'test', 'password')
        self.assertEqual(result, dict(characters=1, claimed=2, deleted=2, failed=1))
        second.select_character.assert_called_once_with(1)
        second.receive_all_mail.assert_called_once_with(delete_after_claim=True)
        first.disconnect.assert_called()
        second.disconnect.assert_called()

    def test_utf_unsigned_length_and_truncation(self):
        value = 'a' * 40000
        self.assertEqual(NSOReader(utf(value)).read_utf(), value)
        with self.assertRaises(EOFError):
            NSOReader(b'\x00\x05abc').read_utf()
        m = NSOMessage(-40)
        m.write_utf(value)
        self.assertEqual(m.get_data(), utf(value))
        self.assertEqual(struct.unpack('>H', m.to_raw_packet()[1:3])[0], 40002)

    def test_invalid_character_does_not_send(self):
        self.client.characters = ['one']
        self.assertFalse(self.client.select_character(-1))
        self.client.sock.sendall.assert_not_called()


if __name__ == '__main__':
    unittest.main()
