import { MeasurementWithUnitPipe } from './measurement-with-unit.pipe';

describe('MeasurementWithUnitPipe', () => {
  [
    { input: 86.3, unit: 'kg', output: '86.3 kg' },
    { input: 86.3, unit: '%', output: '86.3 %' },
    { input: 0, unit: 'kg', output: '0 kg' },
    { input: 86.3, unit: '', output: '-' },
    { input: undefined, unit: 'kg', output: '-' },
  ].forEach(({ input, unit, output }) =>
    it('formats ${output}', () => {
      const pipe = new MeasurementWithUnitPipe();
      expect(pipe.transform(input, unit)).toBe(output);
    })
  );
});
