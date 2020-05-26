import abc

from edifact.edifact_exception import EdifactValidationException
from edifact.models.segment import Segment


class BaseSegmentTestHelper(abc.ABC):
    """
    A base test for subclasses of Segment. Implementor must also inherit unittest.TestCase.
    """
    @abc.abstractmethod
    def _create_segment(self) -> Segment:
        """
        :return: a valid Segment with all attributes populated, created with given parameters
        """
        pass

    @abc.abstractmethod
    def _create_segment_from_string(self) -> Segment:
        """
        :return: a valid Segment with all attributes populated, generated from edifact segment string
        """

    @abc.abstractmethod
    def _get_attributes(self):
        """
        :return: all of the Segments settable attributes
        """
        pass

    def _get_expected_segment(self):
        """
        :return: the expected segment from EDIFACT returned by _create_segment_from_string()
        """
        return self._create_segment()

    @abc.abstractmethod
    def _get_expected_edifact(self):
        """
        :return: the expected EDIFACT for the segment returned by _create_segment()
        """
        pass

    @abc.abstractmethod
    def _compare_segments(self, expected_segment: Segment, actual_segment: Segment):
        """
        compares the two segments and raises an AssertionError if they are not the same
        """
        pass

    def test_to_edifact(self):
        segment = self._create_segment()
        edifact = segment.to_edifact_string()
        self.assertEqual(self._get_expected_edifact(), edifact)

    def test_from_string(self):
        segment = self._create_segment_from_string()
        expected_segment = self._get_expected_segment()
        self._compare_segments(expected_segment, segment)

    def test_missing_attributes(self):
        self.__test_missing_properties(self._get_attributes(), self._create_segment)

    def __test_missing_properties(self, attribute_names: list, generator):
        """
        :param test: instance of the test case
        :param attribute_names: names of all required attributes in the Segment
        :param generator: a no-arg function that generates an instance of the Segment
        :return:
        """
        for attr_name in attribute_names:
            instance = generator()
            setattr(instance, attr_name, None)
            with self.assertRaises(EdifactValidationException, msg=f'missing "{attr_name}" did not fail validation') as ctx:
                instance.to_edifact_string()
            self.assertEqual(f'{instance.key}: Attribute {attr_name} is required', ctx.exception.args[0])
