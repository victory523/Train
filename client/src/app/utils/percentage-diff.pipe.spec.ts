import { PercentageDiffPipe } from './percentage-diff.pipe';

describe('PercentageDiffPipe', () => {
  [
    { input: 0.34566, output: '↑ 34.6 %' },
    { input: 0.001, output: '↑ 0.1 %' },
    { input: 0.0001, output: '-' },
    { input: undefined, output: '-' },
    { input: -0.31234, output: '↓ 31.2 %' },
    { input: -0.001, output: '↓ 0.1 %' },
    { input: -0.0001, output: '-' },
  ].forEach(({ input, output }) =>
    it('formats ${output}', () => {
      const pipe = new PercentageDiffPipe();
      expect(pipe.transform(input)).toBe(output);
    })
  );
});
